package br.com.cwi.eduardo;

import br.com.cwi.eduardo.bean.Currency;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class Main {
    
    public static void main(String[] args) {
        Main m = new Main();
        System.out.println(m.currencyQuotation("AUD", "CAD", 15, "11/10/2018").toString());
        System.out.println(m.currencyQuotation("CAD", "AUD", 15, "11/10/2018").toString());
        System.out.println(m.currencyQuotation("AUD", "CLF", 15, "11/10/2018").toString());
        System.out.println(m.currencyQuotation("CAD", "HKD", 15, "11/10/2018").toString());
    }
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public BigDecimal currencyQuotation(String from, String to, Number value, String quotation) {
        final LocalDate ld = LocalDate.parse(quotation, DATE_FORMAT);
        List<Currency> cs = loadCurrencies(ld, false);
        validate(cs, from, to, value, quotation);
        Optional<Currency> optCur = cs.stream().filter(c -> c.getName().equalsIgnoreCase(from) && (c.getDate().equals(ld))).findFirst();
        if (optCur.isPresent()) {
            Currency fromQuotation = optCur.get();
            printCurrency(fromQuotation);
            optCur = cs.stream().filter(c -> c.getName().equalsIgnoreCase(to) && (c.getDate().equals(ld))).findFirst();
            if (optCur.isPresent()) {
                Currency toQuotation = optCur.get();
                printCurrency(toQuotation);
                return calcCurrency(toQuotation, fromQuotation, new BigDecimal(value.doubleValue()));
            } else {
                throw new IllegalArgumentException("No quotation found for reported date!");
            }
        } else {
            throw new IllegalArgumentException("No quotation found for reported currency!");
        }
    }
    
    private void validate(List<Currency> currencies, String from, String to, Number value, String quotation) {
        if (from == null && to == null && value == null && quotation == null) {
            throw new IllegalArgumentException("All parameters must be filled!");
        }
        
        if (currencies.isEmpty()) {
            throw new IllegalArgumentException("No quotation found for reported date!");
        }
        
        if (currencies.stream().noneMatch(c -> c.getName().equalsIgnoreCase(from))) {
            throw new NoSuchElementException("No quotation found for reported currency!");
        }
        
        if (currencies.stream().noneMatch(c -> c.getName().equalsIgnoreCase(to))) {
            throw new NoSuchElementException("No quotation found for reported currency!");
        }
        
        if (value.doubleValue() < 0d) {
            throw new NumberFormatException("The value must be greater than 0.00!");
        }
    }
    
    private List<Currency> loadCurrencies(LocalDate quotationDate, boolean secondChance) {
        List<Currency> currencies = new ArrayList<>();
        
        int dayOfWeek = quotationDate.getDayOfWeek().getValue();
        if (dayOfWeek != 6 && dayOfWeek != 7) {
            byte[] returnDownload = downloadFile(quotationDate);
            if (returnDownload != null) {
                List<String[]> lines = loadFile(returnDownload);
                
                for (String[] line : lines) {
                    Currency cur = new Currency();
                    cur.setCod(Long.valueOf(line[1]));
                    cur.setName(line[3]);
                    cur.setType(line[2]);
                    cur.setDate(LocalDate.parse(line[0], DATE_FORMAT));
                    cur.setParity(new BigDecimal(line[7].replace(",", ".")));
                    currencies.add(cur);
                }
            } else if (!secondChance) {
                if (dayOfWeek == 1) {
                    loadCurrencies(quotationDate.minusDays(3), true);
                } else {
                    loadCurrencies(quotationDate.minusDays(1), true);
                }
            }
        } else {
            throw new IllegalArgumentException();
        }
        
        return currencies;
    }
    
    public void printCurrency(Currency c) {
        System.out.println("\nCod: " + c.getCod());
        System.out.println("Name: " + c.getName());
        System.out.println("Type: " + c.getType());
        System.out.println("Parity: " + c.getParity().toString());
        System.out.println("Date: " + c.getDate().format(DATE_FORMAT));
        System.out.println("");
    }
    
    private List<String[]> loadFile(byte[] file) {
        BufferedReader br = null;
        List<String[]> registryCSV = new ArrayList<>();
        String linha = "";
        try {
            br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(file)));
            br.readLine();
            while ((linha = br.readLine()) != null) {
                registryCSV.add(linha.split(";"));
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
        BigDecimal valueReturn;
        RoundingMode rm = RoundingMode.HALF_EVEN;
        if (from.getType().equalsIgnoreCase("A")) {
            valueReturn = value.divide(from.getParity(), 6, rm);
            if (to.getType().equalsIgnoreCase("A")) {
                valueReturn = valueReturn.multiply(to.getParity());
            } else {
                valueReturn = valueReturn.divide(to.getParity(), 6, rm);
            }
        } else {
            valueReturn = value.multiply(from.getParity());
            if (to.getType().equalsIgnoreCase("A")) {
                valueReturn = valueReturn.multiply(to.getParity());
            } else {
                valueReturn = valueReturn.divide(to.getParity(), 6, rm);
            }
        }
    
        return valueReturn.setScale(2, rm);
    }
    
    
    private byte[] downloadFile(LocalDate ld) {
        try {
            String date = ld.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String pathUrl = "https://www4.bcb.gov.br/Download/fechamento/" + date + ".csv";
            URL url = new URL(pathUrl);
            InputStream is = url.openStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            int umByte;
            while ((umByte = is.read()) != -1) {
                baos.write(umByte);
            }
            is.close();
            baos.close();
            
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
