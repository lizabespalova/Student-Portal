package com.studentportal.helpbot.service.command;

import com.studentportal.helpbot.service.command.callbackquerycommands.BotHasQueryCommand;
import com.studentportal.helpbot.service.command.hasmessagecommands.BotHasMessageCommand;
import com.studentportal.helpbot.service.command.hasnotnullmessagecommands.BotHasNotNullMessageCommand;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface Command {

    void resolve(Update update);

    boolean apply(Update update);


    @Component
            //@AllArgsConstructor
    class CommandFactory {
        private List<BotHasMessageCommand> botHasMessageCommands;
        private List<BotHasQueryCommand> botHasQueryCommands;
        private List<BotHasNotNullMessageCommand> botHasNotNullMessageCommands;

        @Lazy
        public CommandFactory(List<Command> commands, List<BotHasMessageCommand> botHasMessageCommands, List<BotHasQueryCommand> botHasQueryCommands, List<BotHasNotNullMessageCommand> botHasNotNullMessageCommands) {
            this.botHasMessageCommands = botHasMessageCommands;
            this.botHasQueryCommands = botHasQueryCommands;
            this.botHasNotNullMessageCommands = botHasNotNullMessageCommands;
        }

        public Command getCommand(Update update, byte check) {
//            for (Command command : commands) {
//                if (command.apply(update)) {
//                    return command;
//                }
//            }
            switch (check) {
                case 1: {
                    for (var command : botHasQueryCommands) {
                        if (command.apply(update)) {
                            return command;
                        }
                    }
                    break;
                }
                case 2: {
                    for (var command : botHasNotNullMessageCommands) {
                        if (command.apply(update)) {
                            return command;
                        }
                    }
                    break;
                }
                case 3: {
                    for (var command : botHasMessageCommands) {
                        if (command.apply(update)) {
                            return command;
                        }
                    }
                    break;
                }
                default:
                    throw new IllegalArgumentException("Command not found total Number of Commands");
            }
            throw new IllegalArgumentException("Command not found total Number of Commands");
        }

    }
}
