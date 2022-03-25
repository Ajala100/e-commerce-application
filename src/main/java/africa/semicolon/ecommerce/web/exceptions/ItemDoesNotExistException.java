package africa.semicolon.ecommerce.web.exceptions;

public class ItemDoesNotExistException extends Throwable{
    public ItemDoesNotExistException(String message){
        super(message);
    }
}
