/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.output;

import java.util.List;
import java.util.Objects;

public class Connection<T> {
    private List<Edge<T>> edges;

    private PageInfo pageInfo;

    public Connection(List<Edge<T>> edges, PageInfo pageInfo) {
        this.edges = Objects.requireNonNull(edges);
        this.pageInfo = Objects.requireNonNull(pageInfo);
    }

    public List<Edge<T>> getEdges() {
        return this.edges;
    }

    public PageInfo getPageInfo() {
        return this.pageInfo;
    }
}
