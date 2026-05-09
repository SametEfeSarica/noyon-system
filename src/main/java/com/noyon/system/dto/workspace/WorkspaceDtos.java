package com.noyon.system.dto.workspace;

import lombok.*;

public class WorkspaceDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkspaceResponse {
        private Long id;
        private String name;
        private String color;
        private Integer position;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateWorkspaceRequest {
        private String name;
        private String color;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateWorkspaceRequest {
        private String name;
        private String color;
    }
}
