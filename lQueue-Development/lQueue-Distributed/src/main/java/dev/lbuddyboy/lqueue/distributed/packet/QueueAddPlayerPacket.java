package dev.lbuddyboy.lqueue.distributed.packet;

import com.google.gson.JsonObject;
import dev.lbuddyboy.lqueue.api.model.DistributionType;
import dev.lbuddyboy.lqueue.api.model.QueuePlayer;
import dev.lbuddyboy.lqueue.distributed.pidgin.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QueueAddPlayerPacket implements Packet {

    private JsonObject object = new JsonObject();

    public QueueAddPlayerPacket(String queue, QueuePlayer player, DistributionType type) {

        this.object.addProperty("queue", queue);
        this.object.add("player", player.serialize());
        this.object.addProperty("distribution", type.name());
    }

    @Override
    public int id() {
        return 100000;
    }

    @Override
    public JsonObject serialize() {
        return object;
    }

    @Override
    public void deserialize(JsonObject object) {
        this.object = object;
    }

    public String getQueue() {
        return this.object.get("queue").getAsString();
    }

    public QueuePlayer getQueuePlayer() {
        return QueuePlayer.deserialize(this.object.get("player").getAsJsonObject());
    }

    public DistributionType distributionType() {
        return DistributionType.valueOf(this.object.get("distribution").getAsString());
    }

}
