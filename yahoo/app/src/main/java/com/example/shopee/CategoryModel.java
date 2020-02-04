package com.example.shopee;

public class CategoryModel {

    private String  CategoryIconlink;
    private String CategoryName;

    public CategoryModel(String categoryIconlink, String categoryName) {
        CategoryIconlink = categoryIconlink;
        CategoryName = categoryName;
    }

    public String getCategoryIconlink() {
        return CategoryIconlink;
    }

    public void setCategoryIconlink(String categoryIconlink) {
        CategoryIconlink = categoryIconlink;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }
}