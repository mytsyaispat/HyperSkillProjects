package antifraud.controller;

import antifraud.entity.StolenCard;
import antifraud.entity.SuspicionIp;
import antifraud.entity.TransactionEntity;
import antifraud.entity.response.TransactionResponse;
import antifraud.service.antifraud.AntifraudService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/antifraud")
public class AntifraudController {

    private final AntifraudService antifraudService;

    public AntifraudController(AntifraudService antifraudService) {
        this.antifraudService = antifraudService;
    }

    @PostMapping("/transaction")
    public ResponseEntity<Map<String, String>> antiFraudTransaction(@Valid @RequestBody TransactionEntity transaction) {
        return antifraudService.antiFraudTransaction(transaction);
    }

    @PutMapping("/transaction")
    public ResponseEntity<?> addFeedback(@RequestBody Map<String, Object> data) {
        return antifraudService.addFeedback(data);
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransactionResponse>> getHistoryOfTransactions() {
        return antifraudService.getHistoryOfTransactions();
    }

    @GetMapping("/history/{number}")
    public ResponseEntity<List<TransactionResponse>> getHistoryOfTransactionsByNumber(@PathVariable String number) {
        return antifraudService.getHistoryOfTransactionsByNumber(number);
    }

    @PostMapping("/suspicious-ip")
    public ResponseEntity<SuspicionIp> saveSuspicionIp(@RequestBody Map<String, String> ip) {
        return antifraudService.saveSuspicionIp(ip);
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public ResponseEntity<Map<String, String>> deleteSuspicionIp(@PathVariable String ip) {
        return antifraudService.deleteSuspicionIp(ip);
    }

    @GetMapping("/suspicious-ip")
    public ResponseEntity<List<SuspicionIp>> showSuspiciousIp() {
        return antifraudService.showSuspiciousIp();
    }

    @PostMapping("/stolencard")
    public ResponseEntity<StolenCard> saveStolenCard(@RequestBody Map<String, String> number) {
        return antifraudService.saveStolenCard(number);
    }

    @DeleteMapping("/stolencard/{number}")
    public ResponseEntity<Map<String, String>> deleteStolenCard(@PathVariable String number) {
        return antifraudService.deleteStolenCard(number);
    }

    @GetMapping("/stolencard")
    public ResponseEntity<List<StolenCard>> showStolenCards() {
        return antifraudService.showStolenCards();
    }

}

