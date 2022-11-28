package com.cheremnov.bot.command.add_user;

import com.cheremnov.bot.command.AbstractCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

/**
 * @author cheremnov
 * Date: 14.11.2022
 */
public class AddUser extends AbstractCommand {
    public static final String COMMAND_NAME = "add_user";

    private boolean needChain;

    public AddUser(List<String> args) {
        super(COMMAND_NAME, args);
    }

    @Override
    public void doAction(SendMessage message) {
        if (args.size() == 2) {
            // ��� ��������� ��������, ��������� ������������ � ������ ����������
            trustedUsers.put(Long.valueOf(args.get(0)), args.get(1));
        } else {
            needChain = true;
        }
        super.doAction(message);
    }

    @Override
    protected String getMessageText() {
        return  needChain ? """
                ��� ���������� ����������� � ������ ���������� ��������� ����� ��� ��������� � ���� ���
                """ : "������������ " + args.get(0) + " (" +  args.get(1) + ") ������� �������� � ������ ����������";
    }

    @Override
    public Class<? extends AbstractCommand> nextCommand() {
        return needChain ? HandleReplayMessage.class : null;
    }
}
