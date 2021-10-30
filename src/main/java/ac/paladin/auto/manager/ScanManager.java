package ac.paladin.auto.manager;

import ac.paladin.auto.event.PlayerAbortScanEvent;
import ac.paladin.auto.model.http.StartScanRequest;
import ac.paladin.auto.model.http.StartScanResponse;
import ac.paladin.auto.model.scan.IScan;
import ac.paladin.auto.model.scan.Scan;
import ac.paladin.auto.service.IPaladinService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.*;
import java.util.function.Consumer;

@RequiredArgsConstructor
public final class ScanManager extends WebSocketListener implements IScanManager {

    private final ObjectMapper i_objectMapper;

    private final IPaladinService i_paladinService;

    private final OkHttpClient i_httpClient;

    private final PluginManager i_pluginManager;

    private final List<Scan> m_scans = new ArrayList<>();

    private final Set<String> m_subscriptions = new HashSet<>();

    private WebSocket m_webSocket;

    @Override
    public void connect() {
        Request request = new Request.Builder()
                .url("wss://ws.paladin.ac")
                .build();

        i_httpClient.newWebSocket(request, this);
    }

    @Override
    public void createScan(Player scanner, Player target, Consumer<IScan> consumer) {
        Scan scan = new Scan();
        scan.setTargetId(target.getUniqueId());

        m_scans.add(scan);

        Call<StartScanResponse> call = i_paladinService.startScan(new StartScanRequest(scanner.getUniqueId()));
        call.enqueue(new Callback<StartScanResponse>() {
            @Override
            public void onResponse(Call<StartScanResponse> call, Response<StartScanResponse> response) {
                StartScanResponse body = response.body();
                if (body == null) {
                    return;
                }

                scan.setId(body.getId());
                scan.setLink(body.getDownloadLink());
                scan.setResultsLink(body.getResultsLink());
                scan.setPin(body.getPin());
                subscribe(scan.getId());

                consumer.accept(scan);
            }

            @Override
            public void onFailure(Call<StartScanResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void abortScan(Player player) {
        m_scans.removeIf(scan -> {
            if (scan.getTargetId().equals(player.getUniqueId())) {
                scan.abort();
                return true;
            }

            return false;
        });
        i_pluginManager.callEvent(new PlayerAbortScanEvent(player));
    }

    private void sendMessage(Consumer<ObjectNode> consumer) {
        ObjectNode node = i_objectMapper.createObjectNode();
        consumer.accept(node);

        try {
            m_webSocket.send(i_objectMapper.writeValueAsString(node));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void subscribe(String id) {
        m_subscriptions.add(id);
        sendMessage(node -> node.put("subscribe", id));
    }

    private void unsubscribe(String id) {
        m_subscriptions.remove(id);
        sendMessage(node -> node.put("unsubscribe", id));
    }

    @Override
    public void dispose() {
        m_scans.clear();
        m_subscriptions.clear();
    }

    @Override
    public void onOpen(WebSocket webSocket, okhttp3.Response response) {
        this.m_webSocket = webSocket;

        for (String subscription : m_subscriptions) {
            subscribe(subscription);
        }
    }

    private Optional<Scan> getScanById(String id) {
        return m_scans.stream().filter(it -> it.getId().equals(id)).findFirst();
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            JsonNode node = i_objectMapper.readTree(text);
            JsonNode event = node.get("event");
            String scanId = event.get("scan").asText();
            String type = event.get("type").asText();

            switch (type) {
                case "inprogress":
                    getScanById(scanId).ifPresent(Scan::start);
                    break;

                case "progress":
                    int progress = event.get("data").asInt();
                    getScanById(scanId).ifPresent(scan -> scan.updateProgress(progress));
                    break;

                case "abort":
                    unsubscribe(scanId);
                    getScanById(scanId).ifPresent(Scan::abort);
                    m_scans.removeIf(scan -> scan.getId().equals(scanId));
                    break;

                case "complete":
                    unsubscribe(scanId);
                    getScanById(scanId).ifPresent(Scan::complete);
                    m_scans.removeIf(scan -> scan.getId().equals(scanId));
                    break;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        connect();
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
        t.printStackTrace();
    }
}
