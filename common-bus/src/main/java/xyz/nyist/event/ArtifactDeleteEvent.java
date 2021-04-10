package xyz.nyist.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;

/**
 * @Author : fucong
 * @Date: 2021-04-08 14:17
 * @Description :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArtifactDeleteEvent extends RemoteApplicationEvent {

    private String path;

    public ArtifactDeleteEvent(Object source, String originService, String path) {
        super(source, originService, "cim-file-server");
        this.path = path;
    }
}
