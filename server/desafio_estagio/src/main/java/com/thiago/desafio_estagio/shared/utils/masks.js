(() => {
    const MASKS = {
        cpf:      { mask: '000.000.000-00' },
        cnpj:     { mask: '00.000.000/0000-00' },
        cep:      { mask: '00000-000' },
        rg:       { mask: '00.000.000-A', definitions: { 'A': /[0-9Xx]/ } },
        telefone: {
            mask: [
                { mask: '(00) 0000-0000' },
                { mask: '(00) 00000-0000' }
            ],
            dispatch: (appended, dynamicMasked) => {
                const digits = (dynamicMasked.value + appended).replace(/\D/g, '');
                return dynamicMasked.compiledMasks[digits.length > 10 ? 1 : 0];
            }
        }
    };

    window.initMasks = (root) => {
        if (typeof IMask === 'undefined') return;
        (root || document).querySelectorAll('[data-mask]:not([data-imask-initialized])').forEach(el => {
            const type = el.getAttribute('data-mask');
            const config = MASKS[type];
            if (config) {
                IMask(el, config);
                el.setAttribute('data-imask-initialized', '1');
            }
        });
    };

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => initMasks(document));
    } else {
        initMasks(document);
    }
})();
