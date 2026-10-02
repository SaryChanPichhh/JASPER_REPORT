package com.aureumgrand.report.infrastructure.jasper.datasource.pos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PosPoListingDataSource {
    private LocalDate receiveDate;
    private List<PosPoListing> items;
}
