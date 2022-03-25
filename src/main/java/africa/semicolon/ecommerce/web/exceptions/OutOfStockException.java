package africa.semicolon.ecommerce.web.exceptions;

public class OutOfStockException extends Throwable{
    public OutOfStockException(String message){
        super(message);
    }
}
