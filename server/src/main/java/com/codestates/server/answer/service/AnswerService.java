package com.codestates.server.answer.service;

import com.codestates.server.answer.dto.AnswerResponseDto;
import com.codestates.server.answer.entity.Answer;
import com.codestates.server.answer.repository.AnswerRepository;
import com.codestates.server.question.entity.Question;
import com.codestates.server.question.service.QuestionService;
import com.codestates.server.user.entity.User;
import com.codestates.server.user.repository.UserRepository;
import com.codestates.server.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionService questionService;
    private final UserService userService;

    public AnswerService(AnswerRepository answerRepository, QuestionService questionService, UserService userService) {
        this.answerRepository = answerRepository;
        this.questionService = questionService;
        this.userService = userService;
    }

    public Answer createAnswer(Answer answer, Long questionId, Long userId) {
        Question question = questionService.findQuestion(questionId);
        User user = userService.getUser(userId);

        answer.setQuestion(question);
        answer.setUser(user);

        answerRepository.save(answer);
        return answer;
    }

    public Answer updateAnswer(Answer answer, long questionId, long userId) {
        Question question = questionService.findQuestion(questionId);
        Answer existingAnswer = findAnswerById(answer.getAnswerId());

        if (existingAnswer != null) {

            if(existingAnswer.getUser().getUserId() == userId) {
                existingAnswer.setContent(answer.getContent());
                answerRepository.save(existingAnswer);
                return existingAnswer;
            }else throw new RuntimeException("유저가 다릅니다.");
        }

        throw new EntityNotFoundException("답변이 검색되지 않습니다.");
    }

    public List<Answer> findByQuestionId(long questionId) {
        return answerRepository.findByQuestionId(questionId);
    }

    public void deleteAnswer(long questionId, long answerId, long userId) {
        Answer existingAnswer = findAnswerById(answerId);

        if (existingAnswer != null) {
            if (existingAnswer.getQuestion().getQuestionId() == questionId && existingAnswer.getUser().getUserId() == userId) {
                answerRepository.deleteById(answerId);
            } else {
                throw new IllegalArgumentException("답변이 해당 질문에 연결되어 있지 않습니다.");
            }
        } else {
            throw new EntityNotFoundException("답변이 검색되지 않습니다.");
        }
    }

    public Answer findAnswerById(long answerId) {
        return answerRepository.findById(answerId).orElse(null);
    }
}