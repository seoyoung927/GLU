import { useEffect, useState } from 'react';
import ProblemContentText from '@/components/test/problem/problemContentText';
import ProblemHeader from '@/components/test/problem/problemHeader';
import ProblemOptionList from '@/components/test/problem/problemOptionList';
import PrimaryButton from '@/components/common/buttons/primaryButton';
import { Problem } from '@/types/ProblemTypes';
import { useRouter } from 'next/router';
import ProblemProgressBar from '@/components/test/problem/problemProgressBar';
import ProblemMemoManager from '@/components/test/problem/memo/problemMemoManager';
import ProblemSolvedNavigation from '@/components/test/problem/problemNavigationManager';
import {
  getRecommendedLevelTestProblemsAPI,
  getRecommendedTestProblemsAPI,
  postTestProblemGradingAPI,
} from '@/utils/problem/test';
import { useDispatch } from 'react-redux';
import { levelUp } from '@/store/levelupSlice';
import Loading from '@/components/common/loading';
import ProblemInputField from '@/components/test/problem/problemInputField';
import ProblemImageOptionList from '@/components/test/problem/problemImageOptionList';
import { GetServerSideProps } from 'next';
// eslint-disable-next-line import/no-extraneous-dependencies
import { getCookie } from 'cookies-next';
// eslint-disable-next-line import/no-extraneous-dependencies
import jwt from 'jsonwebtoken';
import { login } from '@/store/authSlice';
import Head from 'next/head';
import styles from './testProblems.module.css';

interface ProblemAnswer {
  problemId: string;
  userAnswer: string; // 사용자의 선택
  problemAnswer: string; // 문제의 정답
  solvedTime?: number; // 풀이 시간 (선택적)
}

interface TestProps {
  initialProblems: Problem[];
}

export const getServerSideProps: GetServerSideProps = async (context) => {
  const accessToken = getCookie(
    'accessToken',
    context ? { req: context.req, res: context.res } : {},
  );

  // accessToken이 존재하는지 확인
  if (!accessToken) {
    return { props: { initialProblems: [] } }; // 기본값으로 빈 배열 전달
  }

  try {
    const { isFirst } = jwt.decode(accessToken as string) as {
      isFirst: boolean;
    };

    if (isFirst) {
      const res = await getRecommendedLevelTestProblemsAPI(context);
      return { props: { initialProblems: res.data } };
    }
  } catch (error) {
    return { props: { initialProblems: [] } };
  }

  const res = await getRecommendedTestProblemsAPI(context);
  return { props: { initialProblems: res.data } };
};

export default function Test({ initialProblems }: TestProps) {
  const router = useRouter();
  const dispatch = useDispatch();
  const [problems] = useState<Problem[]>(initialProblems);
  const [currentProblemIndex, setCurrentProblemIndex] = useState<number>(0); // 현재 문제 인덱스
  const [answers, setAnswers] = useState<ProblemAnswer[]>([]);
  // 푼 문제 개수 계산
  const solvedCount = answers.filter(
    (answer) => answer.userAnswer !== '',
  ).length;
  const [startTime, setStartTime] = useState<number>(Date.now()); // 문제 시작 시간
  const [totalSolvedTime, setTotalSolvedTime] = useState<number>(0);
  const currentProblem = problems[currentProblemIndex];
  const progressPercentage =
    problems.length > 0 ? Math.floor((solvedCount / problems.length) * 90) : 90;

  // 문제 풀이 로직 ///////////////////////////////////////////////////////////////////////////
  const updateAnswers = (
    problemIndex: string,
    updatedFields: Partial<ProblemAnswer>,
  ) => {
    setAnswers((prevAnswers) => {
      const updatedAnswers = prevAnswers.map((answer) =>
        answer.problemId === problemIndex
          ? { ...answer, ...updatedFields }
          : answer,
      );
      return updatedAnswers;
    });
  };

  useEffect(() => {
    const initializeAnswers = async () => {
      const initialAnswers = problems.map((problem) => ({
        problemId: problem.problemId,
        problemAnswer: problem.solution,
        userAnswer: '', // 기본값은 0
        solvedTime: 0, // 기본 풀이 시간은 0
      }));
      setAnswers(initialAnswers);
    };

    if (problems.length > 0) {
      initializeAnswers();
    }
  }, [problems]);

  useEffect(() => {
    window.scrollTo({ top: 0, behavior: 'smooth' });

    const start = Date.now();
    setStartTime(start);

    return () => {
      if (problems.length === 0) return;

      const timeSpent = Math.floor((Date.now() - start) / 1000);
      setTotalSolvedTime(
        (prevTotalSolvedTime) => prevTotalSolvedTime + timeSpent,
      );

      updateAnswers(problems[currentProblemIndex].problemId, {
        solvedTime: (answers[currentProblemIndex]?.solvedTime || 0) + timeSpent,
      });
    };
  }, [currentProblemIndex]);

  const handleAnswer = (problemIndex: string, userAnswer: string) => {
    updateAnswers(problemIndex, { userAnswer });
  };

  const handleSubmit = async () => {
    const timeSpent = Math.floor((Date.now() - startTime) / 1000);

    updateAnswers(problems[currentProblemIndex].problemId, {
      solvedTime: (answers[currentProblemIndex]?.solvedTime || 0) + timeSpent,
    });

    const problemSolveRequests = answers.map((answer) => ({
      problemId: answer.problemId.toString(),
      userAnswer: answer.userAnswer,
      solvedTime: answer.solvedTime || 0,
    }));

    try {
      const res = await postTestProblemGradingAPI(
        totalSolvedTime,
        problemSolveRequests,
      );

      if (res.data.isStageUp) {
        dispatch(levelUp({ level: 2, levelImage: res.data.stageUpUrl }));
      }
      dispatch(login({ isFirst: false }));
      router.push(`/test/result/${res?.data?.testId}`);
    } catch (error) {
      console.error('정답 제출 중 오류 발생:', error);
    }
  };

  // 문제 네비게이션 ///////////////////////////////////////////////////////////////////////////
  const handleNextProblem = () => {
    if (currentProblemIndex < problems.length - 1) {
      setCurrentProblemIndex((prevIndex) => prevIndex + 1);
    }
  };

  const handlePrevProblem = () => {
    if (currentProblemIndex > 0) {
      setCurrentProblemIndex((prevIndex) => prevIndex - 1);
    }
  };

  const handlProblemIndex = (index: number) => {
    setCurrentProblemIndex(index);
  };

  // 렌더링 로직 ///////////////////////////////////////////////////////////////////////////
  if (problems.length === 0 || answers.length === 0) {
    return (
      <div className={styles.container}>
        <Loading size="large" showText />
      </div>
    ); // 로딩 중일 때 표시할 메시지
  }

  return (
    <div className={styles.container}>
      <Head>
        <title>종합 테스트</title>
        <meta property="og:title" content="종합 테스트" />
        <meta
          property="og:description"
          content="총 15문제로, 모든 유형이 포함되어 나의 문해력을 종합적으로 평가합니다."
        />
        <meta property="og:type" content="website" />
        <link rel="icon" type="image/png" sizes="48x48" href="/favicon.png" />
        <link rel="icon" type="image/x-icon" href="/favicon.ico" />
      </Head>

      <div className={styles['problem-container']}>
        <div className={styles['problem-section']}>
          <div className={styles['problem-navigation']}>
            <ProblemSolvedNavigation
              answers={answers}
              currentProblemIndex={currentProblemIndex}
              onProblemIndexChange={handlProblemIndex}
            />
          </div>
          {currentProblem && (
            <div className={styles.problem}>
              <ProblemHeader
                problemId={currentProblem.problemId}
                problemIndex={currentProblemIndex + 1}
                problemLevel={currentProblem?.problemLevel?.code}
                problemType={currentProblem?.problemType?.name}
                problemTitle={currentProblem?.title}
              />
              <div className={styles['problem-content']}>
                <ProblemContentText problemContent={currentProblem?.content} />
                {currentProblem.problemTypeDetail.code === 'PT0311' && (
                  <ProblemImageOptionList
                    problemOptions={
                      Array.isArray(currentProblem.metadata.options)
                        ? currentProblem.metadata.options // string[]일 경우
                        : [currentProblem.metadata.options] // string일 경우 배열로 변환
                    }
                    problemId={answers[currentProblemIndex]?.problemId}
                    selectedOption={answers[currentProblemIndex]?.userAnswer}
                    onTestProblemAnswer={handleAnswer}
                  />
                )}
                {currentProblem.questionType.code === 'QT02' && (
                  <ProblemInputField
                    placeholder={
                      Array.isArray(currentProblem.metadata.options)
                        ? currentProblem.metadata.options.join(', ') // string[]일 경우, 문자열로 변환 (쉼표로 연결된 문자열)
                        : currentProblem.metadata.options // string일 경우 그대로 사용
                    }
                    initialAnswer={answers[currentProblemIndex].userAnswer}
                    problemId={answers[currentProblemIndex]?.problemId}
                    onTestProblemAnswer={handleAnswer}
                  />
                )}
                {currentProblem.problemTypeDetail.code !== 'PT0311' &&
                  currentProblem.questionType.code !== 'QT02' && (
                    <ProblemOptionList
                      problemOptions={
                        Array.isArray(currentProblem.metadata.options)
                          ? currentProblem.metadata.options // string[]일 경우
                          : [currentProblem.metadata.options] // string일 경우 배열로 변환
                      }
                      problemId={answers[currentProblemIndex]?.problemId}
                      selectedOption={answers[currentProblemIndex]?.userAnswer}
                      onTestProblemAnswer={handleAnswer}
                    />
                  )}
              </div>
              <div className={styles['problem-button-list']}>
                {currentProblemIndex > 0 ? (
                  <PrimaryButton
                    size="medium"
                    label="이전 문제"
                    onClick={handlePrevProblem}
                  />
                ) : (
                  <div />
                )}
                {currentProblemIndex === problems.length - 1 ? (
                  <PrimaryButton
                    size="medium"
                    label="제출하기"
                    onClick={handleSubmit}
                  />
                ) : (
                  <PrimaryButton
                    size="medium"
                    label="다음 문제"
                    onClick={handleNextProblem}
                  />
                )}
              </div>
            </div>
          )}
          <div className={styles['problem-memo']}>
            <ProblemMemoManager problemId={currentProblem.problemId} />
          </div>
        </div>

        <ProblemProgressBar progressPercentage={progressPercentage} />
      </div>
    </div>
  );
}
