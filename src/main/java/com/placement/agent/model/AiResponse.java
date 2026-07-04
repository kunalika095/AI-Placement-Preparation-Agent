package com.placement.agent.model;

import java.util.List;

/**
 * Model representing the structured response from the Gemini AI.
 * Encapsulates HR, technical, coding, and aptitude questions, alongside roadmap and preparation tips.
 */
public class AiResponse {

    private List<String> hrQuestions;
    private List<String> technicalQuestions;
    private List<CodingQuestion> codingQuestions;
    private List<AptitudeQuestion> aptitudeQuestions;
    private List<String> preparationTips;
    private List<RoadmapPhase> learningRoadmap;

    public List<String> getHrQuestions() {
        return hrQuestions;
    }

    public void setHrQuestions(List<String> hrQuestions) {
        this.hrQuestions = hrQuestions;
    }

    public List<String> getTechnicalQuestions() {
        return technicalQuestions;
    }

    public void setTechnicalQuestions(List<String> technicalQuestions) {
        this.technicalQuestions = technicalQuestions;
    }

    public List<CodingQuestion> getCodingQuestions() {
        return codingQuestions;
    }

    public void setCodingQuestions(List<CodingQuestion> codingQuestions) {
        this.codingQuestions = codingQuestions;
    }

    public List<AptitudeQuestion> getAptitudeQuestions() {
        return aptitudeQuestions;
    }

    public void setAptitudeQuestions(List<AptitudeQuestion> aptitudeQuestions) {
        this.aptitudeQuestions = aptitudeQuestions;
    }

    public List<String> getPreparationTips() {
        return preparationTips;
    }

    public void setPreparationTips(List<String> preparationTips) {
        this.preparationTips = preparationTips;
    }

    public List<RoadmapPhase> getLearningRoadmap() {
        return learningRoadmap;
    }

    public void setLearningRoadmap(List<RoadmapPhase> learningRoadmap) {
        this.learningRoadmap = learningRoadmap;
    }

    /**
     * Inner class representing a Coding Question structure.
     */
    public static class CodingQuestion {
        private String title;
        private String problemStatement;
        private String constraints;
        private String sampleInput;
        private String sampleOutput;
        private String explanation;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getProblemStatement() {
            return problemStatement;
        }

        public void setProblemStatement(String problemStatement) {
            this.problemStatement = problemStatement;
        }

        public String getConstraints() {
            return constraints;
        }

        public void setConstraints(String constraints) {
            this.constraints = constraints;
        }

        public String getSampleInput() {
            return sampleInput;
        }

        public void setSampleInput(String sampleInput) {
            this.sampleInput = sampleInput;
        }

        public String getSampleOutput() {
            return sampleOutput;
        }

        public void setSampleOutput(String sampleOutput) {
            this.sampleOutput = sampleOutput;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }
    }

    /**
     * Inner class representing an Aptitude Question structure.
     */
    public static class AptitudeQuestion {
        private String question;
        private List<String> options;
        private String correctAnswer;
        private String explanation;

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public void setCorrectAnswer(String correctAnswer) {
            this.correctAnswer = correctAnswer;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }
    }

    /**
     * Inner class representing a Phase in the Learning Roadmap.
     */
    public static class RoadmapPhase {
        private String phase;
        private String duration;
        private List<String> topics;
        private String resourcesOrTips;

        public String getPhase() {
            return phase;
        }

        public void setPhase(String phase) {
            this.phase = phase;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public List<String> getTopics() {
            return topics;
        }

        public void setTopics(List<String> topics) {
            this.topics = topics;
        }

        public String getResourcesOrTips() {
            return resourcesOrTips;
        }

        public void setResourcesOrTips(String resourcesOrTips) {
            this.resourcesOrTips = resourcesOrTips;
        }
    }
}
