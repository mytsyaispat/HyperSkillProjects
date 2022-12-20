package antifraud.service.antifraud;

import antifraud.entity.StolenCard;
import antifraud.entity.SuspicionIp;
import antifraud.entity.TransactionEntity;
import antifraud.entity.enums.Limits;
import antifraud.entity.enums.Regions;
import antifraud.entity.enums.TransactionEnum;
import antifraud.entity.response.TransactionResponse;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.SuspicionIpRepository;
import antifraud.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Stream;

@Service
public class AntifraudServiceImpl implements AntifraudService {

    private final SuspicionIpRepository suspicionIpRepository;
    private final StolenCardRepository stolenCardRepository;
    private final TransactionRepository transactionRepository;

    public AntifraudServiceImpl (SuspicionIpRepository suspicionIpRepository, StolenCardRepository stolenCardRepository, TransactionRepository transactionRepository) {
        this.suspicionIpRepository = suspicionIpRepository;
        this.stolenCardRepository = stolenCardRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> antiFraudTransaction(TransactionEntity transaction) {
        if (!isCorrectIp(transaction.getIp()) || !luhnValidation(transaction.getNumber())
                || !isRegionCorrect(transaction.getRegion().toUpperCase())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        String info;
        StolenCard stolenCard = stolenCardRepository.findByNumber(transaction.getNumber());
        SuspicionIp suspicionIp = suspicionIpRepository.findByIp(transaction.getIp());
        List<String> infoListP = new ArrayList<>();

        int transactionsIp = transactionRepository.countUniqueTransactionsByIp(transaction.getNumber(), transaction.getIp(), transaction.getDateLikeLocalDateTime().minusHours(1), transaction.getDateLikeLocalDateTime());
        int transactionsNumber = transactionRepository.countUniqueTransactionsByRegion(transaction.getNumber(), transaction.getRegion(), transaction.getDateLikeLocalDateTime().minusHours(1), transaction.getDateLikeLocalDateTime());

        if (transactionsIp > 2) infoListP.add("ip-correlation");
        if (transactionsNumber > 2) infoListP.add("region-correlation");
        if (transaction.getAmount() > Limits.MANUAL_PROCESSING.getValue()) infoListP.add("amount");
        if (stolenCard != null) infoListP.add("card-number");
        if (suspicionIp != null) infoListP.add("ip");

        if (infoListP.isEmpty()) {
            List<String> infoListM = new ArrayList<>();
            if (transactionsIp == 2) infoListM.add("ip-correlation");
            if (transactionsNumber == 2) infoListM.add("region-correlation");
            if (transaction.getAmount() > Limits.ALLOWED.getValue()) infoListM.add("amount");
            if (!infoListM.isEmpty()) {
                Collections.sort(infoListM);
                info = infoListM.get(0);
                for (int i = 1; i < infoListM.size(); i++)
                    info = info.concat(", ".concat(infoListM.get(i)));
                transaction.setResult(TransactionEnum.MANUAL_PROCESSING.name());
                transactionRepository.save(transaction);
                return ResponseEntity.ok(Map.of("result", TransactionEnum.MANUAL_PROCESSING.name(), "info", info));
            }
            transaction.setResult(TransactionEnum.ALLOWED.name());
            transactionRepository.save(transaction);
            return ResponseEntity.ok(Map.of("result", TransactionEnum.ALLOWED.name(), "info", "none"));
        }

        Collections.sort(infoListP);
        info = infoListP.get(0);
        for (int i = 1; i < infoListP.size(); i++)
            info = info.concat(", ".concat(infoListP.get(i)));
        transaction.setResult(TransactionEnum.PROHIBITED.name());
        transactionRepository.save(transaction);
        return ResponseEntity.ok(Map.of("result", TransactionEnum.PROHIBITED.name(), "info", info));
    }

    @Override
    public ResponseEntity<?> addFeedback(Map<String, Object> data) {
        long transactionId = Long.parseLong(data.get("transactionId").toString());
        Optional<TransactionEntity> transactionOptional = transactionRepository.findByTransactionId(transactionId);
        if (transactionOptional.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        String feedback = data.get("feedback").toString().toUpperCase();
        if (!feedback.equals(TransactionEnum.ALLOWED.name()) && !feedback.equals(TransactionEnum.MANUAL_PROCESSING.name()) && !feedback.equals(TransactionEnum.PROHIBITED.name()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        TransactionEntity transaction = transactionOptional.get();
        if (!transaction.getFeedback().equals("")) throw new ResponseStatusException(HttpStatus.CONFLICT);

        String result = transaction.getResult();
        if (result.equals(feedback)) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        else {
            if (result.equals(TransactionEnum.ALLOWED.name())) {
                if (feedback.equals(TransactionEnum.MANUAL_PROCESSING.name())) {
                    transaction.setFeedback(TransactionEnum.MANUAL_PROCESSING.name());
                    Limits.ALLOWED.decreasing(transaction.getAmount());
                } else {
                    transaction.setFeedback(TransactionEnum.PROHIBITED.name());
                    Limits.ALLOWED.decreasing(transaction.getAmount());
                    Limits.MANUAL_PROCESSING.decreasing(transaction.getAmount());
                }
            } else if (result.equals(TransactionEnum.MANUAL_PROCESSING.name())) {
                if (feedback.equals(TransactionEnum.ALLOWED.name())) {
                    transaction.setFeedback(TransactionEnum.ALLOWED.name());
                    Limits.ALLOWED.increasing(transaction.getAmount());
                } else {
                    transaction.setFeedback(TransactionEnum.PROHIBITED.name());
                    Limits.MANUAL_PROCESSING.decreasing(transaction.getAmount());
                }
            } else {
                if (feedback.equals(TransactionEnum.ALLOWED.name())) {
                    transaction.setFeedback(TransactionEnum.ALLOWED.name());
                    Limits.ALLOWED.increasing(transaction.getAmount());
                    Limits.MANUAL_PROCESSING.increasing(transaction.getAmount());
                } else {
                    transaction.setFeedback(TransactionEnum.MANUAL_PROCESSING.name());
                    Limits.MANUAL_PROCESSING.increasing(transaction.getAmount());
                }
            }
        }
        transactionRepository.save(transaction);
        return ResponseEntity.ok(new TransactionResponse(transaction));
    }

    @Override
    public ResponseEntity<List<TransactionResponse>> getHistoryOfTransactions() {
        List<TransactionEntity> transactionEntityList = transactionRepository.findAll();
        if(transactionEntityList.isEmpty()) return ResponseEntity.ok(Collections.emptyList());
        List<TransactionResponse> transactionResponses = new ArrayList<>();
        for (TransactionEntity tE : transactionEntityList) transactionResponses.add(new TransactionResponse(tE));
        transactionResponses.sort(Comparator.comparing(TransactionResponse::getTransactionId));
        return ResponseEntity.ok(transactionResponses);
    }

    @Override
    public ResponseEntity<List<TransactionResponse>> getHistoryOfTransactionsByNumber(String number) {
        if(!luhnValidation(number)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        List<TransactionEntity> transactionEntityList = transactionRepository.findAllByNumber(number);
        if(transactionEntityList.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        List<TransactionResponse> transactionResponses = new ArrayList<>();
        for (TransactionEntity tE : transactionEntityList) transactionResponses.add(new TransactionResponse(tE));
        transactionResponses.sort(Comparator.comparing(TransactionResponse::getTransactionId));
        return ResponseEntity.ok(transactionResponses);
    }

    @Override
    public ResponseEntity<SuspicionIp> saveSuspicionIp(Map<String, String> data) {
        String ip = data.get("ip").trim();
        if (!isCorrectIp(ip)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        SuspicionIp susIp = suspicionIpRepository.findByIp(ip);
        if (susIp != null) throw new ResponseStatusException(HttpStatus.CONFLICT);
        suspicionIpRepository.save(new SuspicionIp(ip));
        susIp = suspicionIpRepository.findByIp(ip);
        return ResponseEntity.ok(susIp);
    }

    @Override
    public ResponseEntity<Map<String, String>> deleteSuspicionIp(String ip) {
        if (!isCorrectIp(ip)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        SuspicionIp susIp = suspicionIpRepository.findByIp(ip);
        if (susIp == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        suspicionIpRepository.delete(susIp);
        return ResponseEntity.ok(Map.of("status", String.format("IP %s successfully removed!", ip)));
    }

    @Override
    public ResponseEntity<List<SuspicionIp>> showSuspiciousIp() {
        List<SuspicionIp> suspicionIpList = suspicionIpRepository.findAll();
        if (suspicionIpList.size() > 1) suspicionIpList.sort(Comparator.comparing(SuspicionIp::getId));
        return ResponseEntity.ok(suspicionIpList);
    }

    @Override
    public ResponseEntity<StolenCard> saveStolenCard(Map<String, String> data) {
        String number = data.get("number");
        if (!luhnValidation(number)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        StolenCard stolenCard = stolenCardRepository.findByNumber(number);
        if (stolenCard != null) throw new ResponseStatusException(HttpStatus.CONFLICT);
        stolenCardRepository.save(new StolenCard(number));
        stolenCard = stolenCardRepository.findByNumber(number);
        return ResponseEntity.ok(stolenCard);
    }

    @Override
    public ResponseEntity<Map<String, String>> deleteStolenCard(String number) {
        if (!luhnValidation(number)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        StolenCard stolenCard = stolenCardRepository.findByNumber(number);
        if (stolenCard == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        stolenCardRepository.delete(stolenCard);
        return ResponseEntity.ok(Map.of("status", String.format("Card %s successfully removed!", number)));
    }

    @Override
    public ResponseEntity<List<StolenCard>> showStolenCards() {
        List<StolenCard> stolenCards = stolenCardRepository.findAll();
        if (stolenCards.size() > 1) stolenCards.sort(Comparator.comparing(StolenCard::getId));
        return ResponseEntity.ok(stolenCards);
    }

    // regex for correct IPv4
    private boolean isCorrectIp(String ip) {
        return ip.matches("(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
    }

    // It validates the credit card number using the Luhn algorithm
    private boolean luhnValidation(String number) {
        if (!number.matches("400000[0-9]{10}")) return false;
        int sum = 0;
        for (int i = 0, num; i < 16; i++) {
            int j = Integer.parseInt(number.substring(i, i + 1));
            if (i % 2 == 0) {
                num = j * 2;
                if (num > 9) num -= 9;
                sum += num;
            } else sum += j;
        }
        return sum % 10 == 0;

    }

    // Is the region in the database
    private boolean isRegionCorrect(String region) {
        return Stream.of(Regions.values())
                .anyMatch((r) -> r.name().equals(region));
    }

}
