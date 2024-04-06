package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TokenBag {
    private List<Token> tokens;
    private int n;

    public synchronized int getRemaining() {
        return tokens.size();
    }

    public TokenBag(int n) {
        this.tokens = new ArrayList<>();
        this.n = n;

        for (int i = 1; i <= n; i++) {
            for (int j = i + 1; j <= n; j++) {
                tokens.add(new Token(i, j));
            }
        }
        Collections.shuffle(tokens);
    }

    public synchronized Token extractToken() {

        if (!tokens.isEmpty()) { 
             
            return tokens.remove(tokens.size() - 1);

        }
        return null;
    }

    public int getN() {
        return n;
    }
}