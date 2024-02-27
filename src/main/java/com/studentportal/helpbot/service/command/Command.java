package com.studentportal.helpbot.service.command;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface Command {

    void resolve(Update update);

    boolean apply(Update update);



    @Component
//    @AllArgsConstructor
    class CommandFactory {

        private final List<Command> commands;
        private Command command;
        @Lazy
        public CommandFactory(List<Command> commands) {
            this.commands = commands;
        }

        public Command getCommand(Update update, byte check) {
//            for (Command command : commands) {
//                if (command.apply(update)) {
//                    return command;
//                }
//            }
            switch (check) {
                case 1: {
                    for (int i = 0; i < 24; i++) {
                        command = commands.get(i);
                        if (command.apply(update)) {
                            return command;
                        }
                    }
                    break;
                }
                case 2: {
                    for (int i = 28; i < 46; i++) {
                        command = commands.get(i);
                        if (command.apply(update)) {
                            return command;
                        }
                    }
                    break;
                }
                case 3: {
                    for (int i = 24; i < 28; i++) {
                        command = commands.get(i);
                        if (command.apply(update)) {
                            return command;
                        }
                    }
                    break;
                }
                default:
                    throw new IllegalArgumentException();
            }
            throw new IllegalArgumentException();
        }
    }
}
