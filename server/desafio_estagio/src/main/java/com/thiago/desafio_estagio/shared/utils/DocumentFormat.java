package com.thiago.desafio_estagio.shared.utils;

public class DocumentFormat {

    private DocumentFormat() {}

    public static String formatCpf(String cpf) {
        if (cpf == null) return "—";
        if (cpf.length() != 11) return cpf;
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    public static String formatCnpj(String cnpj) {
        if (cnpj == null) return "—";
        if (cnpj.length() != 14) return cnpj;
        return cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "."
                + cnpj.substring(5, 8) + "/" + cnpj.substring(8, 12) + "-" + cnpj.substring(12);
    }

    public static String formatDocument(String doc) {
        if (doc == null) return "—";
        String digits = doc.replaceAll("\\D", "");
        if (digits.length() == 11)
            return digits.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        if (digits.length() == 14)
            return digits.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        return doc;
    }

    public static String formatCep(String cep) {
        if (cep == null) return "—";
        if (cep.length() != 8) return cep;
        return cep.substring(0, 5) + "-" + cep.substring(5);
    }

    public static String formatTelefone(String tel) {
        if (tel == null) return "—";
        if (tel.length() == 11)
            return "(" + tel.substring(0, 2) + ") " + tel.substring(2, 7) + "-" + tel.substring(7);
        if (tel.length() == 10)
            return "(" + tel.substring(0, 2) + ") " + tel.substring(2, 6) + "-" + tel.substring(6);
        return tel;
    }
}
