package org.setms.resin.format.yaml;

import org.junit.jupiter.api.Test;
import org.setms.resin.process.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WhenDeserializingFromYaml {

    @Test
    void shouldConstructSoftwareProcessFromYaml() {
        var yaml = """
                process:
                  elements:
                    rdm:
                      type: read-model
                      data:
                        - data
                    usr:
                      type: person
                    cmd:
                      type: command
                    agg:
                      type: aggregate
                      data:
                        - data1
                        - data2
                    evt:
                      type: event
                    apl:
                      type: automatic-policy
                    cmd2:
                      type: command
                    exs:
                      type: external-system
                    evt2:
                      type: event
                  flows:
                    - flow:
                      - rdm
                      - usr
                      - cmd
                      - agg
                      - evt
                      - apl
                      - cmd2
                      - exs
                      - evt2
                    - flow:
                      - rdm
                      - apl
                """;

        var actual = new YamlToSoftwareProcess().apply(yaml);

        var expected = new SoftwareProcess();
        var rdm = expected.element(new ReadModel("rdm", List.of("data")));
        var usr = expected.element(new Person("usr"));
        var cmd = expected.element(new Command("cmd"));
        var agg = expected.element(new Aggregate("agg", List.of("data1", "data2")));
        var evt = expected.element(new Event("evt"));
        var apl = expected.element(new AutomaticPolicy("apl"));
        var cmd2 = expected.element(new Command("cmd2"));
        var exs = expected.element(new ExternalSystem("exs"));
        var evt2 = expected.element(new Event("evt2"));
        expected.connect(rdm, usr, cmd, agg, evt, apl, cmd2, exs, evt2);
        expected.connect(rdm, apl);
        assertEquals(expected, actual);
    }

}
