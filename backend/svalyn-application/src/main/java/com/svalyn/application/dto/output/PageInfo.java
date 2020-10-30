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

    private long count;

    public PageInfo(boolean hasPreviousPage, boolean hasNextPage, long count) {
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

    public long getCount() {
        return this.count;
    }
}
