package com.jsmatos.compare;

import lombok.Data;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Data
public class Kyc {
    @Label("Personal info")
    private PersonalInfo personalInfo;
    @Label("Contact details")
    private ContactDetails contactDetails;
    @Label("Financial info")
    private FinancialInfo financialInfo;

    @Label("Whatevers")
    private List<Whatever> whatevers;

    @Data
    static class ContactDetails {
        @Label("Legal address")
        private Address legalAddress;
        @Label("Correspondence address")
        private Address correspondenceAddress;
    }


    @Data
    static class Whatever {
        @Label("One")
        private String one;
        @Label("Two")
        private String two;

    }

    @Data
    public static class Address {
        @Label("Street")
        @Updatable
        private String street;
        @Label("City")
        private String city;
        @Label("Postal code")
        private String postalCode;
        @Label("Country")
        private String country;
    }

    @Data
    public static class PersonalInfo {
        @Updatable
        @Label("First name")
        private String firstName;
        @Updatable
        @Label("Last name")
        private String lastName;
        @Label("Birth date")
        private LocalDate birthDate;
    }

    @Data
    public static class FinancialInfo {
        @Label("Accounts")
        private Collection<Account> accounts;
        @Label("Total net worth")
        private Long totalNetWorth;

        @Data
        public static class Account implements HasId {
            @Label("IBAN")
            private String id;
            @Updatable
            @Label("Currency")
            private String currency;

            @Label("Source of funds")
            Collection<SourceOfFunds> sourceOfFunds;
        }

        @Data
        public static class SourceOfFunds implements HasId {
            @Label("Source")
            private String source;
            @Label("Comments")
            private String comments;

            @Label("Id")
            private String id;
        }

    }

}
