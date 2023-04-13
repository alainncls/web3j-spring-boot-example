package com.consensys.web3j;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.util.Arrays;
import java.util.List;

import static java.lang.System.out;

@SpringBootApplication
public class Web3jApplication {
    @Value("${infuraApiKey}")
    private String infuraApiKey;

    private final String contractAddress = "0x2Ec762F47829b3C46c58bcC6d25d74E9Ed0e110C";

    public static void main(String[] args) {
        SpringApplication.run(Web3jApplication.class, args);
    }

    @PostConstruct
    public void afterInitialize() {

        Web3j web3j = Web3j.build(new HttpService("https://sepolia.infura.io/v3/" + infuraApiKey));

        // Define the event we want to listen
        final Event createdEvent = new Event("Created",
                Arrays.asList(
                        new TypeReference<Utf8String>(true) {
                        },
                        new TypeReference<Address>(true) {
                        }));

        // Encode the event
        final String encodedEvent = EventEncoder.encode(createdEvent);

        // Create a filter on latest block for the listened contract
        EthFilter filter = new EthFilter(DefaultBlockParameter.valueOf(Numeric.toBigInt("321464")), DefaultBlockParameterName.LATEST, contractAddress).addSingleTopic(encodedEvent);

        // Listen to events
        web3j.ethLogFlowable(filter).subscribe(event -> {
            List<String> topics = event.getTopics();
            String eventSignature = topics.get(0);
            String hashedFilmId = topics.get(1);
            Address deFilmFunding = (Address) FunctionReturnDecoder.decodeIndexedValue(topics.get(2), new TypeReference<Address>() {
            });

            out.println("Event signature is " + (eventSignature.equals(encodedEvent) ? "matching" : "not matching" + " what we want"));
            out.println("Block #" + event.getBlockNumber().toString() + " contains a film creation with contract deployed at " + deFilmFunding + " and hashed film ID is " + hashedFilmId);
        });
    }
}
