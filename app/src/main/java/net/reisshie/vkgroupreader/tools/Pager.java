/**
 * Created by Alexey on 17.12.2016.
 */

package net.reisshie.vkgroupreader.tools;

public class Pager {
    private boolean strict;
    private boolean isSetItemsTotal;
    private int firstItemIndex;
    private int pageNumber;
    private int pageSize;
    private int itemsTotal;
    private int pagesTotal;

    public Pager() {
        this.reset();
    }

    public Pager reset() {
        this.isSetItemsTotal = false;
        this.firstItemIndex = 0;
        this.pageNumber = 0;
        this.pageSize = 0;
        this.itemsTotal = 0;
        this.pagesTotal = 0;
        return this;
    }

    // region setters

    /**
     * Set strict mode.
     * If strict mode is on then user cannot call getTotalPages and getTotalItems before
     * setTotalCount call. Additionally all input will be checked.
     *
     * @param isStrict
     * @return
     */
    public Pager setStrict(boolean isStrict) {
        this.strict = isStrict;
        return this;
    }

    /**
     * Set first page index
     * @param index
     * @return
     */
    public Pager setFirstPageIndex(int index) {
        this.firstItemIndex = index;
        return this;
    }

    /**
     * Set current page number
     * @param pageNumber
     * @return
     */
    public Pager setCurrentPage(int pageNumber) {
        if(this.strict && pageNumber < 0) {
            this.pageNumber = 0;
        } else {
            this.pageNumber = pageNumber;
        }
        return this;
    }

    /**
     * Set page size
     * @param pageSize
     * @return
     */
    public Pager setPageSize(int pageSize) {
        if(this.strict && pageSize < 0) {
            this.pageSize = 0;
        } else {
            this.pageSize = pageSize;
        }
        return this;
    }

    /**
     * Set count of items
     * @param totalItemsCount
     * @return
     */
    public Pager setTotalCount(int totalItemsCount) {
        if(this.strict && totalItemsCount < 0) {
            this.itemsTotal = 0;
        } else {
            this.itemsTotal = totalItemsCount;
        }
        this.isSetItemsTotal = true;
        return this;
    }
    // endregion setters

    // region getters
    public int getLimit() {
        return this.pageSize;
    }

    public int getOffset() {
        return (this.pageSize * this.pageNumber);
    }

    public int getCurrentPage() {
        return this.pageNumber + this.firstItemIndex;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getTotalPages() throws NumberFormatException {
        if(this.strict && ! this.isSetItemsTotal) {
            throw new NumberFormatException("You must set itemsTotal before using pager!");
        }
        return (this.pageSize == 0 ? 0 : (int)(Math.ceil(this.itemsTotal / this.pageSize)));
    }

    public int getTotalItems() throws NumberFormatException {
        if(this.strict && ! this.isSetItemsTotal) {
            throw new NumberFormatException("You must set itemsTotal before using pager!");
        }
        return this.itemsTotal;
    }
    // endregion getters

    public int nextPage() {
        this.pageNumber++ ;
        return this.pageNumber;
    }
}
