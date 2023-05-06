package de.unistuttgart.iste.gits.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChapterMapperTest {

    private ChapterMapper chapterMapper;

    @BeforeEach
    public void setUp() {
        chapterMapper = new ChapterMapperImpl();
    }
}
