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
});
