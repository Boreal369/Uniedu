package br.ucsal.uniedu.controller;

import br.ucsal.uniedu.dto.Dtos;
import br.ucsal.uniedu.service.BackupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/backup")
public class BackupController {

    private final BackupService backupService;

    public BackupController(BackupService backupService) { this.backupService = backupService; }

    // RNF005
    @GetMapping
    public List<Dtos.BackupInfo> listar() { return backupService.listar(); }

    // RNF005
    @PostMapping
    public ResponseEntity<Dtos.BackupInfo> executar() {
        return ResponseEntity.status(HttpStatus.CREATED).body(backupService.executar());
    }
}
