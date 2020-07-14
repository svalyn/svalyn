/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.output;

import java.util.Objects;

public class Edge<T> {
    private T node;

    public Edge(T node) {
        this.node = Objects.requireNonNull(node);
    }

    public T getNode() {
        return this.node;
    }
}
