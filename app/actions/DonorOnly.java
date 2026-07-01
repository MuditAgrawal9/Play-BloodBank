package actions;

import play.mvc.With;

import java.lang.annotation.*;

@With(DonorOnlyAction.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DonorOnly {}
