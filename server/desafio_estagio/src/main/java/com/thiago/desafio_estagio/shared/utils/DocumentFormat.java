package com.thiago.desafio_estagio.shared.utils;

public class DocumentFormat {

    private DocumentFormat() {}

    public static String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf != null ? cpf : "—";
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    public static String formatarCnpj(String cnpj) {
        if (cnpj == null || cnpj.length() != 14) return cnpj != null ? cnpj : "—";
        return cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "."
                + cnpj.substring(5, 8) + "/" + cnpj.substring(8, 12) + "-" + cnpj.substring(12);
    }

    public static String formatarDocumento(String doc) {
        if (doc == null) return "—";
        String digits = doc.replaceAll("\\D", "");
        if (digits.length() == 11)
            return digits.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        if (digits.length() == 14)
            return digits.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        return doc;
    }

    public static String formatarCep(String cep) {
        if (cep == null || cep.length() != 8) return cep != null ? cep : "—";
        return cep.substring(0, 5) + "-" + cep.substring(5);
    }

    public static String formatarTelefone(String tel) {
        if (tel == null) return "—";
        if (tel.length() == 11)
            return "(" + tel.substring(0, 2) + ") " + tel.substring(2, 7) + "-" + tel.substring(7);
        if (tel.length() == 10)
            return "(" + tel.substring(0, 2) + ") " + tel.substring(2, 6) + "-" + tel.substring(6);
        return tel;
    }
}
