package app.business.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Getter
public class Priority {

    private Drink drink;
    private LocalDateTime localDateTime;

    public Priority(Drink drink) {
        this.drink = drink;
        this.localDateTime = LocalDateTime.now();
    }
}
