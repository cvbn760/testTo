package com.space.specifications;

public class SearchCriteria
{
    private String key;  // Имя поля сущности
    private Object value; // Значение поля сущности
    private SearchOperaton searchOperaton; // Операция поиска

    public SearchCriteria(String key, Object value, SearchOperaton searchOperaton)
    {
        key = key.trim();
        this.key = key;
        this.value = value;
        this.searchOperaton = searchOperaton;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public SearchOperaton getSearchOperaton()
    {
        return searchOperaton;
    }

    public void setSearchOperaton(SearchOperaton searchOperaton)
    {
        this.searchOperaton = searchOperaton;
    }

    @Override
    public String toString()
    {
        return "SearchCriteria{" +
                "key='" + key + '\'' +
                ", value=" + value +
                ", searchOperaton=" + searchOperaton +
                '}';
    }
}
