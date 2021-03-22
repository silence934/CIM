package xyz.nyist.bo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import xyz.nyist.constant.AuthConstants;
import xyz.nyist.entity.UserEntity;
import xyz.nyist.utils.JsonUtil;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @Author : fucong
 * @Date: 2021-03-07 19:09
 * @Description :
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CimAuthentication implements Authentication {

    private Collection<GrantedAuthority> authorities;
    private Integer id;

    private String userName;

    private UserEntity userEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return "[PROTECT]";
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return id;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return userName;
    }


    public static CimAuthentication converter(String str) {
        JsonObject jsonObject = JsonUtil.string2JsonObject(str);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (JsonElement item : jsonObject.get(AuthConstants.JWT_AUTHORITIES_KEY).getAsJsonArray()) {
            authorities.add(new SimpleGrantedAuthority(item.getAsString()));
        }

        return CimAuthentication.builder()
                .id(jsonObject.get("id").getAsInt())
                .userName(jsonObject.get("userName").getAsString())
                .authorities(authorities)
                .build();
    }
}
