/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.output;

public class PageInfo {
    private boolean hasPreviousPage;

    private boolean hasNextPage;

    private int count;

    public PageInfo(boolean hasPreviousPage, boolean hasNextPage, int count) {
        this.hasPreviousPage = hasPreviousPage;
        this.hasNextPage = hasNextPage;
        this.count = count;
    }

    public boolean isHasPreviousPage() {
        return this.hasPreviousPage;
    }

    public boolean isHasNextPage() {
        return this.hasNextPage;
    }

    public int getCount() {
        return this.count;
    }
}
