package com.olafparfienczyk.mortgageplan.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.olafparfienczyk.mortgageplan.entity.dto.NewCustomerDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@ConditionalOnProperty(
        prefix = "customer.demo",
        value = "from-csv"
)
@Component
public class FromCsvFileDemoCustomersProvider implements DemoCustomersProvider {

    private final List<NewCustomerDTO> cachedCustomers;

    public FromCsvFileDemoCustomersProvider(@Value("${customer.demo.from-csv}") String location) throws IOException {
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        CsvMapper mapper = new CsvMapper();
        URL locationURL = ResourceUtils.getURL(location);
        try (MappingIterator<NewCustomerDTO> iterator =
                     mapper
                             .readerFor(NewCustomerDTO.class)
                             .with(schema)
                             .readValues(locationURL)) {
            this.cachedCustomers = iterator.readAll();
        }
    }

    @Override
    public List<NewCustomerDTO> getDemoCustomers() {
        return cachedCustomers
                .stream()
                .map(NewCustomerDTO::new)
                .toList();
    }
}
