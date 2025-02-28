package com.oussama.FacultyPlanning.Event;

import com.oussama.FacultyPlanning.User.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter @Getter
public class RegistrationCompleteEvent extends ApplicationEvent {
    private User user;
    private String applicationURL;

    public RegistrationCompleteEvent(User user , String applicationURL) {
        super(user);
        this.user = user;
        this.applicationURL = applicationURL;
    }
}
