package security;

import models.enums.Role;
import play.libs.typedmap.TypedKey;

public class JwtAttrs {

  public static final TypedKey<Long> USER_ID = TypedKey.create("userId");

  public static final TypedKey<Role> ROLE = TypedKey.create("role");
}
