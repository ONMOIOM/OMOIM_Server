package backend.onmoim.domain.auth.service.command;

import backend.onmoim.domain.auth.dto.request.EmailAuthRequestDTO;
import backend.onmoim.domain.auth.dto.response.EmailAuthResponseDTO;

public interface EmailAuthCommandService {
    EmailAuthResponseDTO.VerificationResultDTO sendCode(EmailAuthRequestDTO.SendCodeDTO request);
}
