package antifraud.service.antifraud;

import antifraud.entity.StolenCard;
import antifraud.entity.SuspicionIp;
import antifraud.entity.TransactionEntity;
import antifraud.entity.response.TransactionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Service
public interface AntifraudService {
    ResponseEntity<Map<String, String>> antiFraudTransaction(TransactionEntity transaction);
    ResponseEntity<?> addFeedback(Map<String, Object> data);
    ResponseEntity<List<TransactionResponse>> getHistoryOfTransactions();
    ResponseEntity<List<TransactionResponse>> getHistoryOfTransactionsByNumber(String number);


    ResponseEntity<SuspicionIp> saveSuspicionIp(Map<String, String> ip);
    ResponseEntity<Map<String, String>> deleteSuspicionIp(String ip);
    ResponseEntity<List<SuspicionIp>> showSuspiciousIp();

    ResponseEntity<StolenCard> saveStolenCard(Map<String, String> number);
    ResponseEntity<Map<String, String>> deleteStolenCard(String number);
    ResponseEntity<List<StolenCard>> showStolenCards();
}