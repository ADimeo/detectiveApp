package de.hpi3d.gamepgrog.trap.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Endpoints in {@link de.hpi3d.gamepgrog.trap.api.ApiManager.ServerApi}
 * annotated with this will not be send by the {@link ApiCall} if safety-mode is on
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UploadsData {}
