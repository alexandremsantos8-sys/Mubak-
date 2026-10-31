// Abre e fecha o menu em telas pequenas, mantendo aria-expanded sincronizado.
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.mobile-menu').forEach((button) => {
        const navigation = button.closest('.site-nav');
        button.addEventListener('click', () => {
            const expanded = navigation.classList.toggle('is-open');
            button.setAttribute('aria-expanded', String(expanded));
        });
    });

    document.querySelectorAll('.product-carousel-track').forEach((track) => {
        const section = track.closest('.home-products');
        const buttons = section.querySelectorAll('[data-carousel-direction]');

        const updateButtons = () => {
            const maxScroll = track.scrollWidth - track.clientWidth;
            buttons.forEach((button) => {
                const direction = Number(button.dataset.carouselDirection);
                button.disabled = direction < 0
                    ? track.scrollLeft <= 2
                    : track.scrollLeft >= maxScroll - 2;
            });
        };

        buttons.forEach((button) => {
            button.addEventListener('click', () => {
                const card = track.querySelector('.carousel-product-card');
                const gap = parseFloat(getComputedStyle(track).columnGap) || 0;
                const distance = card ? (card.getBoundingClientRect().width + gap) * 2 : track.clientWidth * 0.8;
                track.scrollBy({ left: distance * Number(button.dataset.carouselDirection), behavior: 'smooth' });
            });
        });

        track.addEventListener('scroll', updateButtons, { passive: true });
        window.addEventListener('resize', updateButtons);
        updateButtons();
    });

    const paymentModal = document.querySelector('#payment-modal');
    const policyModal = document.querySelector('#policy-modal');
    const modals = [paymentModal, policyModal].filter(Boolean);
    const openModal = (modal) => { modal.hidden = false; document.body.classList.add('modal-open'); modal.querySelector('.modal-close')?.focus(); };
    const closeModal = (modal) => { modal.hidden = true; if (modals.every((item) => item.hidden)) document.body.classList.remove('modal-open'); };
    document.querySelector('[data-open-payment]')?.addEventListener('click', () => openModal(paymentModal));
    document.querySelector('[data-open-policy]')?.addEventListener('click', () => openModal(policyModal));
    document.querySelectorAll('[data-close-modal]').forEach((button) => button.addEventListener('click', () => closeModal(button.closest('.modal'))));
    document.addEventListener('keydown', (event) => { if (event.key === 'Escape') modals.filter((modal) => !modal.hidden).forEach(closeModal); });

    document.querySelectorAll('[data-payment-option]').forEach((option) => option.addEventListener('click', () => {
        const fields = document.querySelector(`[data-payment-fields="${option.dataset.paymentOption}"]`);
        const expanded = option.getAttribute('aria-expanded') === 'true';
        document.querySelectorAll('[data-payment-option]').forEach((item) => item.setAttribute('aria-expanded', 'false'));
        document.querySelectorAll('[data-payment-fields]').forEach((item) => { item.hidden = true; });
        if (!expanded) { option.setAttribute('aria-expanded', 'true'); fields.hidden = false; }
    }));

    const digitsOnly = (value) => value.replace(/\D/g, '');
    const mascararCartao = (numero) => {
        const limpo = numero.replace(/\D/g, '');
        if (limpo.length === 16) return limpo.replace(/(\d{4})\d{8}(\d{4})/, '$1 xxxx xxxx $2');
        return numero;
    };
    window.mascararCartao = mascararCartao;
    const cardPatterns = [['Visa', /^4/], ['Mastercard', /^(5[1-5]|2(2[2-9]|[3-6]\d|7[01]|720))/], ['Amex', /^(34|37)/], ['Elo', /^(4011|4312|4389|5067|627780|636297)/], ['Diners Club', /^(30|36|38|39)/], ['Discover', /^(6011|65|64[4-9]|622)/], ['Hipercard', /^(38|60)/], ['JCB', /^35/]];
    document.querySelectorAll('[autocomplete="cc-number"]').forEach((input) => {
        input.addEventListener('input', () => {
            input.dataset.rawValue = digitsOnly(input.value).slice(0, 19);
            input.value = input.dataset.rawValue.replace(/(\d{4})(?=\d)/g, '$1 ').trim();
            const match = cardPatterns.find(([, pattern]) => pattern.test(input.dataset.rawValue));
            input.closest('.card-input-wrap').querySelector('[data-card-brand]').textContent = match ? match[0] : '';
        });
        input.addEventListener('focus', () => { if (input.dataset.rawValue) input.value = input.dataset.rawValue.replace(/(\d{4})(?=\d)/g, '$1 ').trim(); });
        input.addEventListener('blur', () => { if (digitsOnly(input.value).length === 16) input.value = mascararCartao(input.value); });
    });
    document.querySelectorAll('[autocomplete="cc-exp"]').forEach((input) => input.addEventListener('input', () => {
        const value = digitsOnly(input.value).slice(0, 4); input.value = value.length > 2 ? `${value.slice(0, 2)}/${value.slice(2)}` : value;
    }));

    const validCpf = (value) => {
        const cpf = digitsOnly(value); if (cpf.length !== 11 || /^(\d)\1+$/.test(cpf)) return false;
        let sum = 0; for (let index = 0; index < 9; index += 1) sum += Number(cpf[index]) * (10 - index);
        let digit = (sum * 10) % 11; if (digit === 10) digit = 0; if (digit !== Number(cpf[9])) return false;
        sum = 0; for (let index = 0; index < 10; index += 1) sum += Number(cpf[index]) * (11 - index);
        digit = (sum * 10) % 11; if (digit === 10) digit = 0; return digit === Number(cpf[10]);
    };
    const validCnpj = (value) => {
        const cnpj = digitsOnly(value); if (cnpj.length !== 14 || /^(\d)\1+$/.test(cnpj)) return false;
        const check = (length) => { let sum = 0; let weight = length === 12 ? 5 : 6; for (let index = 0; index < length; index += 1) { sum += Number(cnpj[index]) * weight; weight = weight === 2 ? 9 : weight - 1; } return sum % 11 < 2 ? 0 : 11 - (sum % 11); };
        return check(12) === Number(cnpj[12]) && check(13) === Number(cnpj[13]);
    };
    document.querySelectorAll('#pix-document, #boleto-document').forEach((input) => input.addEventListener('input', () => {
        const value = digitsOnly(input.value).slice(0, 14);
        input.value = value.length > 11 ? value.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5') : value.replace(/(\d{3})(\d{3})(\d{3})(\d{1,2})/, '$1.$2.$3-$4');
        input.closest('.payment-fields').querySelector('[data-qr-button]').disabled = input.id === 'boleto-document' ? !validCnpj(value) : !validCpf(value);
    }));
    document.querySelectorAll('[data-qr-button]').forEach((button) => button.addEventListener('click', () => { paymentModal.querySelector('[data-checkout-status]').textContent = 'QR Code gerado. Escaneie para concluir o pagamento.'; }));
    document.querySelectorAll('[data-finish-payment]').forEach((button) => button.addEventListener('click', () => { button.closest('.modal-panel').querySelector('[data-checkout-status]').textContent = 'Pagamento validado. Seu pedido está pronto para confirmação.'; }));
});
