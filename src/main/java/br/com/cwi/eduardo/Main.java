package br.com.cwi.eduardo;

import br.com.cwi.eduardo.bean.Currency;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class Main {
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    
    public Main() {
    }
    
    public BigDecimal currencyQuotation(String from, String to, Number value, String quotation) {
        if (from != null && to != null && value != null && quotation != null) {
            final LocalDate ld = LocalDate.parse(quotation, DATE_FORMAT);
            List<Currency> cs = loadCurrencies();
            if (cs.stream().anyMatch(c -> c.getName().equalsIgnoreCase(from) && c.getName().equalsIgnoreCase(to))) {
                if (value.doubleValue() >= 0d) {
                    int dayOfWeek = ld.getDayOfWeek().getValue();
                    if (dayOfWeek != 6 && dayOfWeek != 7) {
                        Optional<Currency> optCur = cs.stream().filter(c -> c.getName().equalsIgnoreCase(to) && (c.getDate().equals(ld))).findFirst();
                        if (optCur.isPresent()) {
                            Currency curQuotation = optCur.get();
                            printCurrency(curQuotation);
                        } else {
                            if (dayOfWeek == 1) {
                                final LocalDate otherLd = ld.minusDays(3);
                                optCur = cs.stream().filter(c -> c.getName().equalsIgnoreCase(to) && (c.getDate().equals(otherLd))).findFirst();
                            } else {
                                final LocalDate otherLd = ld.minusDays(1);
                                optCur = cs.stream().filter(c -> c.getName().equalsIgnoreCase(to) && (c.getDate().equals(otherLd))).findFirst();
                            }
                            if (optCur.isPresent()) {
                                Currency curQuotation = optCur.get();
                                printCurrency(curQuotation);
                                
                            } else {
                                throw new NoSuchElementException();
                            }
                        }
                    } else {
                        throw new IllegalArgumentException();
                    }
                } else {
                    throw new NumberFormatException();
                }
            } else {
                throw new NoSuchElementException();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    private List<Currency> loadCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        String fileUrl = ClassLoader.getSystemResource("datasource.csv").getFile();
        List<String[]> lines = new Main().loadFile(fileUrl, ";");
        
        for (String[] line : lines) {
            Currency cur = new Currency();
            cur.setCod(Long.valueOf(line[1]));
            cur.setName(line[3]);
            cur.setType(line[2]);
            cur.setDate(LocalDate.parse(line[0], DATE_FORMAT));
            cur.setParity(new BigDecimal(line[7].replace(",", ".")));
            currencies.add(cur);
        }
        
        return currencies;
    }
    
    public void printCurrency(Currency c) {
        System.out.println("Cod: " + c.getCod());
        System.out.println("Name: " + c.getName());
        System.out.println("Type: " + c.getType());
        System.out.println("Parity: " + c.getParity().toString());
        System.out.println("Date: " + c.getDate().format(DATE_FORMAT));
    }
    
    private List<String[]> loadFile(String file, String csvDivisor) {
        BufferedReader br = null;
        List<String[]> registryCSV = new ArrayList<>();
        String linha = "";
        try {
            br = new BufferedReader(new FileReader(file));
            br.readLine();
            while ((linha = br.readLine()) != null) {
                registryCSV.add(linha.split(csvDivisor));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return registryCSV;
    }
    
    private BigDecimal calcCurrency(Currency to, Currency from, BigDecimal value) {
        BigDecimal valueReaisTo = value.multiply(to.getParity());
        BigDecimal valueReaisFrom = value.multiply(from.getParity());
        
        
    }
}
