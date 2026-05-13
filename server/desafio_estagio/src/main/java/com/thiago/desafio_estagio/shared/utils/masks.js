document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.erp-list-col--documento').forEach(function (el) {
        var digits = el.textContent.replace(/\D/g, '');
        if (digits.length === 11) {
            el.textContent = digits.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
        } else if (digits.length === 14) {
            el.textContent = digits.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5');
        }
    });
});
