/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Assessment;
import com.svalyn.application.dto.output.Category;
import com.svalyn.application.dto.output.Requirement;
import com.svalyn.application.dto.output.Test;

@Service
public class AssessmentFactory {
    public Assessment createAssessment() {
        return new Assessment(UUID.randomUUID(), "1.0.0",
                List.of(this.getHardwareCategory(), this.getSoftwareCategory(), this.getSupportCategory()));
    }

    private Category getHardwareCategory() {
        var freeze = new Test(UUID.randomUUID(), "Motor functions can be freezed",
                "On command of a technician, a host should freeze all motor functions", null);
        var diagnosticable = new Requirement(UUID.randomUUID(), "The hosts should be diagnosticable",
                "It is our duty to ensure that we can detect any issue affecting one of our host", List.of(freeze));

        var restraint = new Test(UUID.randomUUID(), "Hosts should be not break from their role",
                "A host should also stay in character to perform its job", null);
        var normal = new Test(UUID.randomUUID(), "Hosts should act as human beings",
                "Guests should not be able to identify that a hosts is an android", null);
        var emotional = new Requirement(UUID.randomUUID(), "The hosts should be emotionally stable",
                "We should care about the emotions of our hosts in order to make sure that they can work properly.",
                List.of(restraint, normal));

        var able = new Test(UUID.randomUUID(), "", "", null);
        var functional = new Requirement(UUID.randomUUID(), "The hosts should work as expected",
                "We have to make sure that our hosts are able to perform their role by having a functional body",
                List.of(able));

        var spare = new Test(UUID.randomUUID(), "We should have spare parts",
                "Our spare parts storage should contain everything needed to repair hosts on a daily basis", null);
        var room = new Test(UUID.randomUUID(), "We should have facilities to repair hosts",
                "We should have enough room to store and repair our hosts", null);
        var trained = new Test(UUID.randomUUID(), "Our staff should be trained",
                "Our technicians should be trained constantly to repair and update any given model", null);
        var repairable = new Requirement(UUID.randomUUID(), "The hosts should be repairable",
                "In case of issue, we should be able to repair any possible damage affecting our hosts",
                List.of(spare, room, trained));

        var description = "The Behavior Lab and Diagnostics division of the Mesa Hub is where hosts are brought to be trained, diagnosed, analyzed, and updated";
        return new Category(UUID.randomUUID(), "Behavior & Diagnostics", description,
                List.of(diagnosticable, emotional, functional, repairable));
    }

    private Category getSoftwareCategory() {
        var doubt = new Test(UUID.randomUUID(), "Guests should not doubt our stories",
                "Our stories should not raise any doubts in the mind of our guests", null);
        var feedback = new Test(UUID.randomUUID(), "We should see good feedback on our stories",
                "Guests should only submit positive feedback on our stories", null);
        var convincing = new Requirement(UUID.randomUUID(), "The stories should be convincing",
                "Our guests should not question anything in our stories", List.of(doubt, feedback));

        var completion = new Test(UUID.randomUUID(), "Our stories should be finishable",
                "Our guests should complete more than 80% of the stories that they start", null);
        var social = new Test(UUID.randomUUID(), "Our stories should be social events",
                "The introduction of a new story should be an event in social networks", null);
        var engaging = new Requirement(UUID.randomUUID(), "The stories should be engaging",
                "Our stories should motivate our guests to complete them", List.of(completion, social));

        var returning = new Test(UUID.randomUUID(), "Our guests should want to return to try new stories",
                "Our guests should want to spend more time in Westworld than in the real world, as such our guests should return at least 3 times per year to one of our parks",
                null);
        var price = new Test(UUID.randomUUID(), "Our guests should be ready to pay anything for our stories",
                "The unique experience that only us can deliver thanks to our park should make our guests be ready to pay anything for the chance to experience it",
                null);
        var addictive = new Requirement(UUID.randomUUID(), "The stories shoud be addictive",
                "Our stories should always keep our guests on the edge to make them look for more",
                List.of(returning, price));

        var merchandising = new Test(UUID.randomUUID(), "Our guests should want to bring our park with them",
                "Our merchandising department should report at least $10.000 of gifts bought per guest", null);
        var immersive = new Test(UUID.randomUUID(), "Our guests should never want to leave our parks",
                "Our parks should be so immersive that at least 10% of our guests should be permanent residents", null);
        var coherent = new Requirement(UUID.randomUUID(), "The environment should be coherent",
                "Our environment are built to serve our stories, as such they should properly serve them",
                List.of(merchandising, immersive));

        var description = "The Narrative and Design division of the Westworld Mesa Hub is headed by Lee Sizemore and is responsible for the creation of Narratives";
        return new Category(UUID.randomUUID(), "Narrative & Design", description,
                List.of(convincing, engaging, addictive, coherent));
    }

    private Category getSupportCategory() {
        var stoppable = new Test(UUID.randomUUID(), "Our stories should be stoppable",
                "In case of emergency, any story should be stoppable in less than 10s anywhere in our parks", null);
        var restartable = new Test(UUID.randomUUID(), "Our stories should be restartable",
                "In case of issue, we should be able to give our guests the ability to restart a story at anytime",
                null);
        var interruptible = new Requirement(UUID.randomUUID(), "The operation should be interruptible",
                "In case of issue, we should be able to interrupt any ongoing story", List.of(stoppable, restartable));

        var transport = new Test(UUID.randomUUID(), "We should be able to evacuate our guests",
                "Some mean of transportation should be available to evacuate our guests in case of emergency", null);
        var medical = new Test(UUID.randomUUID(), "We should be able to provide medical assistance",
                "A medical team should be on alert 24/7 to assist any guest", null);
        var extraction = new Test(UUID.randomUUID(), "We should be able to extract guests from the park",
                "In case of critical emergency, we should be able to dispatch our extraction team to the park", null);
        var retrievable = new Requirement(UUID.randomUUID(), "The guests should be retrievable",
                "We should always be able to retrieve any endangered guests as soon as possible",
                List.of(transport, medical, extraction));

        var sensors = new Test(UUID.randomUUID(), "We need to be able to detect potential issues",
                "A large set of cameras needs to work 24/7 to let us monitor our parks", null);
        var communication = new Test(UUID.randomUUID(), "We need to be able to communicate with anybody in the park",
                "A park-wide communication system is needed to coordinate our teams and to communicate with quests",
                null);
        var monitored = new Requirement(UUID.randomUUID(), "The parks should be monitored",
                "In order to support the activities of the park, we should monitor it extensively to detect any issue",
                List.of(sensors, communication));

        var description = "Quality Assurance (shortened to QA) is a division of Delos Destinations which oversees the standards and safety at Westworld and the other parks";
        return new Category(UUID.randomUUID(), "Quality Assurance", description,
                List.of(interruptible, retrievable, monitored));
    }
}
