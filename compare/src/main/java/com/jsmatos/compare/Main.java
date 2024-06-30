package com.jsmatos.compare;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException {
        List<FieldInfo> fields = ReflectionUtil.getFields(Kyc.class);


        Work work = new Work(fields);
        StringBuilder sb = new StringBuilder();
        sb.append("<table style='border: 1px;'>");
//        sb.append("<tr><td></td><td>before</td><td>→</td><td>after</td><td>→</td><td>updated</td></tr>");
        sb.append(FieldInfo.header());
        String s = work.with(kyc(1), kyc(4), kyc(5));
        sb.append(s);
        sb.append("</table>");

        URL url = Main.class.getResource("/playground.html");
        Path path = Paths.get(url.toURI());
        String template = Files.readString(path, StandardCharsets.UTF_8);
        String output = template.replace("{{table}}", sb.toString());
        Path of = Path.of(path.getParent().toString(), "table.html");
        if (!Files.exists(of)) {
            Files.createFile(of);
        }
        Files.writeString(of, output, StandardOpenOption.TRUNCATE_EXISTING);

    }


    private static Kyc kyc(Object mark) {
        Kyc kyc = new Kyc();
        kyc.setContactDetails(new Kyc.ContactDetails(){{
            Kyc.Address legalAddress = new Kyc.Address() {{
                setCity("CSD" + mark);
                setCountry("CH" + mark);
                setPostalCode("1618" + mark);
                setStreet("LL, 5" + mark);
            }};
            setLegalAddress(legalAddress);
            Kyc.Address correspondenceAddress = new Kyc.Address() {{
                setCity("ca.CSD" + mark);
                setCountry("ca.CH" + mark);
                setPostalCode("ca.1618" + mark);
                setStreet("ca.LL, 5" + mark);
            }};
            setCorrespondenceAddress(correspondenceAddress);

        }});
        Kyc.PersonalInfo personalInfo = new Kyc.PersonalInfo();
        personalInfo.setFirstName("fn" + mark);
        personalInfo.setLastName("ln" + mark);
        personalInfo.setBirthDate(LocalDate.now());
        kyc.setPersonalInfo(personalInfo);
        Kyc.FinancialInfo financialInfo = new Kyc.FinancialInfo();
        Collection<Kyc.FinancialInfo.Account> accounts = new HashSet<>();
        accounts.add(new Kyc.FinancialInfo.Account() {{
            setId("123");
            setCurrency("CHF" + mark);
        }});
        accounts.add(new Kyc.FinancialInfo.Account() {{
            setId("124");
            setCurrency("EUR" + mark);
        }});
        accounts.add(new Kyc.FinancialInfo.Account() {{
            setId("125");
            setCurrency("USD" + mark);
            setSourceOfFunds(Arrays.asList(
                    new Kyc.FinancialInfo.SourceOfFunds() {{
                        setSource("Savings" + mark);
                        setId("1");
                    }},
                    new Kyc.FinancialInfo.SourceOfFunds() {{
                        setSource("Gambling" + mark);
                        setComments("At the casino" + mark);
                        setId("2");
                    }}

            ));
        }});
        financialInfo.setAccounts(accounts);
        kyc.setFinancialInfo(financialInfo);
        kyc.setWhatevers(Arrays.asList(
                new Kyc.Whatever(){{
                    setOne("1");
                    setTwo("2");
                }},
                new Kyc.Whatever(){{
                    setOne("1x");
                    setTwo("2x");
                }}
        ));
        return kyc;
    }

}
