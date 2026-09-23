// Abre e fecha o menu em telas pequenas, mantendo aria-expanded sincronizado.
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.mobile-menu').forEach((button) => {
        const navigation = button.closest('.site-nav');
        button.addEventListener('click', () => {
            const expanded = navigation.classList.toggle('is-open');
            button.setAttribute('aria-expanded', String(expanded));
        });
    });
});
