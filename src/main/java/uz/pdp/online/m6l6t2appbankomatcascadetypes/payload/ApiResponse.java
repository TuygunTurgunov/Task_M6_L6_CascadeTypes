package uz.pdp.online.m6l6t2appbankomatcascadetypes.payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    private String massage;
    private Boolean isSuccess;
    private Object object;

    public ApiResponse(String massage, Boolean isSuccess) {
        this.massage = massage;
        this.isSuccess = isSuccess;
    }
}
