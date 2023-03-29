package com.consensys.web3j;

import io.reactivex.disposables.Disposable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.out;

@SpringBootApplication
public class Web3jApplication {
    @Value("${infuraApiKey}")
    private String infuraApiKey;

    private final String contractAddress = "0xdAC17F958D2ee523a2206206994597C13D831ec7";

    public static void main(String[] args) {
        SpringApplication.run(Web3jApplication.class, args);
    }

    @PostConstruct
    public void afterInitialize() {

        Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/" + infuraApiKey));

        // Listen to blocks
        Disposable blockSubscription = web3j.blockFlowable(false).subscribe(block -> {
            out.println("Block #" + block.getBlock().getNumber().toString() + " with hash = " + block.getBlock().getHash() + " contains " + block.getBlock().getTransactions().size() + " transactions");
        });

        // Define the event we want to listen
        final Event transferEvent = new Event("Transfer",
                Arrays.asList(
                        new TypeReference<Address>(true) {
                        },
                        new TypeReference<Address>(true) {
                        },
                        new TypeReference<Uint256>(false) {
                        }));

        // Encode the event
        final String encodedEvent = EventEncoder.encode(transferEvent);

        // Create a filter on latest block for the listened contract
        EthFilter filter = new EthFilter(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST, contractAddress).addSingleTopic(encodedEvent);

        // Listen to events
        Disposable transferSubscription = web3j.ethLogFlowable(filter).subscribe(event -> {
            List<String> topics = event.getTopics();
            Address from = (Address) FunctionReturnDecoder.decodeIndexedValue(topics.get(1), new TypeReference<Address>() {
            });
            Address to = (Address) FunctionReturnDecoder.decodeIndexedValue(topics.get(2), new TypeReference<Address>() {
            });

            List<Type> nonIndexedValues = FunctionReturnDecoder.decode(
                    event.getData(), transferEvent.getNonIndexedParameters());

            BigDecimal value = BigDecimal.ZERO;
            if (nonIndexedValues.size() == 1) {
                value = new BigDecimal(((Uint256) nonIndexedValues.get(0)).getValue());
            }

            out.println("Block #" + event.getBlockNumber().toString() + " - Transfer " + Convert.fromWei(value, Convert.Unit.MWEI) + " USDT from " + from + " to " + to);
        });
    }
}
