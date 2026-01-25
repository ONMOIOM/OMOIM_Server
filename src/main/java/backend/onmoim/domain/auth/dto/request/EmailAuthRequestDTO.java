package backend.onmoim.domain.auth.dto.request;

import lombok.Builder;

@Builder
public class EmailAuthRequestDTO {
    public record SendCodeDTO(
            String email
    ) {}
}
