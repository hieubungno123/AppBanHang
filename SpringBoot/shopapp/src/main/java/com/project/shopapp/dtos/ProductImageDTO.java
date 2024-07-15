package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO {

    @JsonProperty("product_id")
    @Min(value =1, message = "User Id must be > 0")
    private Long productId;

    @Size(min = 5,max = 200, message = "Url image between 3 and 200 characters")
    @JsonProperty("image_url")
    private String urlImage;
}
