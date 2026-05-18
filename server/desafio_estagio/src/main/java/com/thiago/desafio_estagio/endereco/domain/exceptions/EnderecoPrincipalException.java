package com.thiago.desafio_estagio.endereco.domain.exceptions;

// Lançada quando o usuário tenta deletar o endereço principal ou desmarcá-lo diretamente.
// Sempre deve existir um endereço principal por cliente — para trocar, basta marcar outro como
// principal (a operação desmarca o atual automaticamente).
public class EnderecoPrincipalException extends RuntimeException {

    public EnderecoPrincipalException() {
        super("Não é possível remover ou desmarcar o endereço principal. Marque outro endereço como principal para substituí-lo.");
    }

    public EnderecoPrincipalException(String message) {
        super(message);
    }
}
