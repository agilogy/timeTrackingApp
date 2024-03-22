package com.agilogy.timetracking.app

import com.agilogy.timetracking.domain.TimeTrackingApp
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands


fun startBot(timeTrackingApp: TimeTrackingApp) = app {
    val token = System.getenv("DISCORD_BOT_TOKEN")

    //Create a new JDA instance
    val jda = JDABuilder.createDefault(token)
        .addEventListeners(EventsListener(timeTrackingApp))
        .build()

    //Register our first command /ping (don't use this method to often ;))
    jda.updateCommands().addCommands(
        Commands.slash("ping2", "Repeats messages back to you.")
            .addOption(OptionType.STRING, "message", "The message to repeat.")).queue()

    jda.updateCommands().addCommands(
        Commands.slash("projects", "Returns the list of projects configured.")).queue()



    //Set the activity to "I'm Ready"
    jda.presence.activity = Activity.playing("Agilogy school")
}
class BasicBot {



}