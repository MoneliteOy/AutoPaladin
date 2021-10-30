package ac.paladin.auto.manager;

import ac.paladin.auto.model.IDisposable;
import ac.paladin.auto.model.scan.IScan;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public interface IScanManager extends IDisposable {

    void connect();

    /**
     * Create a new scan for the target player.
     *
     * @param scanner  scanner
     * @param target   target
     * @param consumer scan
     */
    void createScan(Player scanner, Player target, Consumer<IScan> consumer);

    void abortScan(Player player);
}
