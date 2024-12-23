package me.cedric.siegegame.model.player.kits.db.entitiy;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import jakarta.persistence.AttributeConverter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class ItemConverter implements AttributeConverter<ItemStack, String>  {

    private final Gson gson = new Gson();

    @Override
    public String convertToDatabaseColumn(ItemStack attribute) {
        return toBase64(attribute);
    }

    @Override
    public ItemStack convertToEntityAttribute(String dbData) {
        return fromBase64(dbData);
    }

    public ItemStack fromBase64(String base64) {
        try {
            FastByteArrayInputStream inputStream = new FastByteArrayInputStream(Base64Coder.decodeLines(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack)dataInput.readObject();
            dataInput.close();
            return item;
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    public String toBase64(ItemStack item) {
        try {
            FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.array);
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }

}
