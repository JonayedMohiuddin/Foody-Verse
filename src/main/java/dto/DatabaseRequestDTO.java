package dto;

import java.io.Serializable;

public class DatabaseRequestDTO implements Serializable
{
    public enum RequestType
    {
        RESTAURANT_LIST,
        SINGLE_RESTAURANT,
    }

    private final RequestType requestType;

    public DatabaseRequestDTO(RequestType requestType)
    {
        this.requestType = requestType;
    }

    public RequestType getRequestType()
    {
        return requestType;
    }

    @Override
    public String toString()
    {
        return "DatabaseRequestDTO{" + "requestType=" + requestType + '}';
    }
}
