package com.Senai.Mubak.service;

import com.Senai.Mubak.model.produto;
import com.Senai.Mubak.repository.ProdutoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {
    private static final String SESSION_KEY = "mubakCart";
    private final ProdutoRepository produtoRepository;

    public CartService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public void add(HttpSession session, Long productId) {
        produto product = findAvailable(productId);
        Map<Long, Integer> cart = cart(session);
        int nextQuantity = cart.getOrDefault(productId, 0) + 1;
        if (nextQuantity > product.getEstoque()) throw new IllegalArgumentException("Não há estoque suficiente para adicionar mais unidades.");
        cart.put(productId, nextQuantity);
    }

    public void setQuantity(HttpSession session, Long productId, int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("A quantidade mínima é 1. Use Remover para excluir o produto.");
        produto product = findAvailable(productId);
        if (quantity > product.getEstoque()) throw new IllegalArgumentException("A quantidade solicitada excede o estoque disponível.");
        Map<Long, Integer> cart = cart(session);
        if (!cart.containsKey(productId)) throw new IllegalArgumentException("Esse produto não está no carrinho.");
        cart.put(productId, quantity);
    }

    public void remove(HttpSession session, Long productId) {
        cart(session).remove(productId);
    }

    public void clear(HttpSession session) {
        cart(session).clear();
    }

    public Snapshot snapshot(HttpSession session) {
        Map<Long, Integer> cart = cart(session);
        List<Line> lines = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int count = 0;
        Iterator<Map.Entry<Long, Integer>> iterator = cart.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Integer> entry = iterator.next();
            produto product = produtoRepository.findById(entry.getKey()).orElse(null);
            if (product == null || product.getEstoque() == null || product.getEstoque() < 1) {
                iterator.remove();
                continue;
            }
            int quantity = Math.min(entry.getValue(), product.getEstoque());
            if (quantity != entry.getValue()) entry.setValue(quantity);
            BigDecimal lineTotal = product.getPreco().multiply(BigDecimal.valueOf(quantity));
            lines.add(new Line(product, quantity, lineTotal));
            subtotal = subtotal.add(lineTotal);
            count += quantity;
        }
        return new Snapshot(List.copyOf(lines), subtotal, count);
    }

    public int count(HttpSession session) {
        if (session == null) return 0;
        return cart(session).values().stream().mapToInt(Integer::intValue).sum();
    }

    private produto findAvailable(Long productId) {
        produto product = produtoRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        if (product.getEstoque() == null || product.getEstoque() < 1)
            throw new IllegalArgumentException("Este produto está sem estoque.");
        return product;
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> cart(HttpSession session) {
        Object current = session.getAttribute(SESSION_KEY);
        if (current instanceof Map<?, ?>) return (Map<Long, Integer>) current;
        Map<Long, Integer> created = new LinkedHashMap<>();
        session.setAttribute(SESSION_KEY, created);
        return created;
    }

    public record Line(produto product, int quantity, BigDecimal total) { }
    public record Snapshot(List<Line> lines, BigDecimal subtotal, int count) { }
}
