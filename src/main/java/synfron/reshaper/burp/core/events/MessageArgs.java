package synfron.reshaper.burp.core.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import synfron.reshaper.burp.core.events.message.Message;

@Data
@AllArgsConstructor
public class MessageArgs {
    private final Object source;
    private final Message data;
}
